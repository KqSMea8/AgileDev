package com.baidu.spark.exception;

/**
 * 附件相关的异常
 * 
 * @author shixiaolei
 */
public class AttachmentException extends SparkRuntimeException {

	private static final long serialVersionUID = -3332911450640366416L;

	public AttachmentException(OpType opType, Object... arguments) {
		super(opType.getMessageCode(), arguments);
	}

	public AttachmentException(OpType opType, Object[] arguments,
			Throwable cause) {
		super(opType.getMessageCode(), arguments, cause);
	}

	public AttachmentException(OpType opType, Throwable cause) {
		super(opType.getMessageCode() , cause);
	}

	/**
	 * 操作类型(上传(UPLOAD) 、 下载(DOWNLOAD) 、 初始化文件夹(INIT))
	 * 
	 * @author shixiaolei
	 * 
	 */
	public enum OpType {
		/** 上传失败 */
		UPLOAD("card.attachment.failedToUpload"), 
		/** 下载失败 */
		DOWNLOAD("card.attachment.failedToDownload"),
		/** 初始化文件夹失败 */
		INIT("card.attachment.failedToInit");
		private String messageCode;
		OpType(String messageCode) {
			this.messageCode = messageCode;
		}
		private String getMessageCode() {
			return messageCode;
		}
	}

}
