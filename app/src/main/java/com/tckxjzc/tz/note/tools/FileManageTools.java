package com.tckxjzc.tz.note.tools;

import java.io.*;


public class FileManageTools {
	
	/**
	 * 复制文件夹
	 * @param na-被复制文件夹的名字
	 * @param nb-复制到此目录
	 * @return
	 * @throws IOException 
	 */
	public static boolean folderCopy(String na, String nb) throws IOException{
		File fna=new File(na);
		if(!fna.exists()){
			fna.mkdirs();
		}
		
		nb=nb+File.separator+fna.getName();
		File fnb=new File(nb);
		if(!fnb.exists()){
			fnb.mkdirs();
		}
		
		File[] file=fna.listFiles();
		for(File f:file){
			if(f.isFile()){
				//System.out.println(f.getAbsolutePath()+"\t"+nb+"\t"+f.getName());
				fileCopy(f.getAbsolutePath(),nb,f.getName());
			}else if(f.isDirectory()){
				folderCopy(f.getAbsolutePath(),nb);
			}
		}
		
		return true;
	}
	/**
	 * 单个文件复制
	 * @param na 被复制文件名和路径
	 * @param nb 复制到此目录
	 * @param fileName 文件名
	 * @return
	 * @throws IOException
	 */
	public static void fileCopy(String na,String nb,String fileName) throws IOException{
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(na));
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(nb+File.separator+fileName));
		byte[] bye=new byte[1024*1024];
		int length;
		while((length=bis.read(bye))!=-1){
			bos.write(bye,0,length);
		}
		bis.close();
		bos.close();
	}
	/**
	 * 文件夹删除
	 * @param folderName 要删除的文件夹路径及名称
	 * @return
	 */
	public static boolean folderDelete(String folderName){
		File file=new File(folderName);
		if(!file.exists()){
			return true;
		}
		File[] fileList=file.listFiles();
		for(File fl:fileList){
			if(fl.isFile()){
				fl.delete();
			}else if(fl.isDirectory()){
				folderDelete(fl.getAbsolutePath());
			}
		}
		file.delete();
		
		return true;
	}
	/**
	 * 批量重命名
	 * @param fileDirectory 指定文件夹
	 * @param name 新的名称
	 * @param type 文件类型
	 * @return
	 */
	public static boolean renameFile(File fileDirectory,String name,String type){
		File[] file=fileDirectory.listFiles();
		for(int i=0,j=0;i<file.length;i++){
			if(file[i].isFile()){
				j++;
				file[i].renameTo(new File(fileDirectory.getAbsolutePath()+File.separator+name+j+type));
			}
		}
		return true;
	}
	/**
	 * 遍历某个文件夹下所有文件
	 * @param fileDirectory
	 */
	public static void getAllFiles(File fileDirectory,HandleFile handleFile){
		if(!fileDirectory.exists()||!fileDirectory.isDirectory()){
			return;
		}
		File[] files=fileDirectory.listFiles();
		for(File file:files){
			if(file.isFile()){
				handleFile.handleFile(file);
			}else{
				getAllFiles(file,handleFile);
			}
		}
	}
	
	//接口
	/**
	 * 
	 * @author tckxjzc
	 *
	 */
	public static interface HandleFile{
		/**
		 * 对所有遍历出来的所有文件进行操作
		 * @param file 要处理的文件
		 */
		public abstract void handleFile(File file);
	}
}
