package com.zx.template.transform

import com.alibaba.android.arouter.register.utils.ScanUtil
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ddmlib.Log
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.*
import sun.nio.ch.IOUtil

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
/**
 * transform 插入 此工程出包的用户信息
 */
class LogTransform extends Transform{
    private static String userInfo
    private static final String HOOK_CLASS = "com/zx/moduleinit/ModuleInitManager.class"
    private static final String HOOK_METHOD = "dispatchAppInit"

    LogTransform(String info){
        super()
        userInfo = info
        println("zhouxin 获取到的信息$userInfo")
    }
    @Override
    String getName() {
        return "LogTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }
    private ExecutorService mExecutorService = Executors.newCachedThreadPool()

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "zhouxin Log transform start   transformInvocation.inputs.size ${ transformInvocation.inputs.size()}"
        long start = System.currentTimeMillis()
        if (!transformInvocation.isIncremental()){
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation.inputs.each {

            // 处理源码文件
            it.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(transformInvocation.incremental, directoryInput, transformInvocation.outputProvider)
            }
            // 处理jar
//            long jarStart = System.currentTimeMillis()
//            it.jarInputs.each { JarInput jarInput ->
//                println  "jarInput 信息 ${jarInput.file.name} ${jarInput.name}"
//            }
            it.jarInputs.each { JarInput jarInput ->
                handleJarInputs(transformInvocation.incremental, jarInput, transformInvocation.outputProvider)

            }

//            long jarEnd = System.currentTimeMillis()
//            println("zhouxin  Log transform jarInputs  耗时${(jarEnd-jarStart)/1000}")
        }
        println("zhouxin  Log transform  ${transformInvocation.isIncremental()}  end 耗时${(System.currentTimeMillis()-start)/1000}")
    }
    /**
     * 处理源码文件
     */
    static void handleDirectoryInput(boolean incremental, DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        String root = directoryInput.file.absolutePath
        boolean leftSlash = File.separator == '/'
        if (!root.endsWith(File.separator))
            root += File.separator

        if (incremental) {
            Map<File, Status> map = directoryInput.getChangedFiles()
            File dir = directoryInput.file
            for (Map.Entry<File, Status> entry : map.entrySet()) {
                Status status = entry.getValue()
                File file = entry.getKey()
                String destFilePath = file.getAbsolutePath().replace(dir.getAbsolutePath(), dest.getAbsolutePath());
                File destFile = new File(destFilePath)

                println("zhouxin handleDirectoryInput status:$status  name:${file.name}")

                if (status == Status.REMOVED) {
                    destFile.delete()
                } else if (status == Status.CHANGED || status == Status.ADDED) {
                    def path = file.absolutePath.replace(root, '')
                    if (!leftSlash) {
                        path = path.replaceAll("\\\\", "/")
                    }
//                    println("zhouxin handleDirectoryInput $incremental status:$status  path:  $path ")
                    if (HOOK_CLASS == path) {
                        println("zhouxin handleDirectoryInput incremental: $incremental  found")
                        def scanFile = new FileInputStream(file)
                        ClassReader classReader = new ClassReader(scanFile)
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        ClassVisitor cv = new ModifyClassVisitor(classWriter)
                        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                        scanFile.close()
                    }
                }
            }
        } else {
            directoryInput.file.eachFileRecurse { File file ->
                def path = file.absolutePath.replace(root, '')
                if (!leftSlash) {
                    path = path.replaceAll("\\\\", "/")
                }
                if (HOOK_CLASS == path) {
                    println("zhouxin handleDirectoryInput $incremental  found")
                    def scanFile = new FileInputStream(file)
                    ClassReader classReader = new ClassReader(scanFile)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new ModifyClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    scanFile.close()
                }
            }
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

    /**
     * 处理jar
     */
    static void handleJarInputs(boolean incremental, JarInput jarInput, TransformOutputProvider outputProvider) {
        def destName = jarInput.name
        // rename jar files
        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4)
        }

        // input file
        File src = jarInput.file
        // output file
        File dest = outputProvider.getContentLocation(destName+hexName,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)
        if (incremental) {
            if (jarInput.status == Status.REMOVED) {
                if (dest.exists()) {
                    dest.delete()
                }
                return
            }
            if (jarInput.status == Status.NOTCHANGED) {
                return
            }
        }
//        println "zhouxin  incremental ${incremental} 名称  路径  ${src.name}  dest ${dest.name} ,status:  ${jarInput.status}  destName : $destName   , hexName： $hexName  , 合并${destName+hexName}   "

        if (ScanUtil.shouldProcessPreDexJar(src.absolutePath)){
            if (src){
                def optJar = new File(src.getParent(),src.name+".opt")
                if (optJar.exists()){
                    optJar.delete()
                }
                JarFile jarFile = new JarFile(src)
                Enumeration enumeration = jarFile.entries()
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                    String entryName = jarEntry.getName()
                    ZipEntry zipEntry = new ZipEntry(entryName)
                    InputStream inputStream = jarFile.getInputStream(jarEntry)
                    jarOutputStream.putNextEntry(zipEntry)

                    if (HOOK_CLASS == entryName) {
                        println("zhouxin handleJarInputs $incremental  found")

                        ClassReader classReader = new ClassReader(inputStream)
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        ClassVisitor cv = new ModifyClassVisitor(classWriter)
                        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                        jarOutputStream.write(classWriter.toByteArray())
                    }else{
                        jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    }
                    inputStream.close()
                    jarOutputStream.closeEntry()
                }
                jarOutputStream.close()
                jarFile.close()

                if (src.exists()){
                    src.delete()
                }
                optJar.renameTo(src)
            }
        }
//        println("zhouxin copyFile src ${src.absolutePath}  dest ${dest.absolutePath}")
        FileUtils.copyFile(src, dest)
    }



    static class ModifyClassVisitor extends ClassVisitor {

        // todo: 改成 asm7 试试
        ModifyClassVisitor(ClassWriter api) {
            super(Opcodes.ASM7, api)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (HOOK_METHOD == name) {
                return new ModifyMethodVisitor(mv);
            }
            return mv
        }
    }
    static class ModifyMethodVisitor extends MethodVisitor {

        ModifyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM7, mv);
        }

        /**
         * 方法开始时调用
         */
        @Override
        void visitCode() {
            super.visitCode()
            visitLdcInsn("zhouxin1")
            visitLdcInsn(userInfo)
            visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "w", "(Ljava/lang/String;Ljava/lang/String;)I", false);

        }

        /**
         * 方法结束时调用
         */
        @Override
        void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                mv.visitLdcInsn("zhouxin2")
                mv.visitLdcInsn(userInfo)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "w", "(Ljava/lang/String;Ljava/lang/String;)I", false);

            }
            super.visitInsn(opcode)
        }


    }

}