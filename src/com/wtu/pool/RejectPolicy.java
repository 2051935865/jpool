package com.wtu.pool;

/**
 * �ܾ�����
 * RejectPolicy.java
 * Author: �ŷ�����
 * Date: 2019��11��5��
 * Description: TODO
 *
 */
public interface RejectPolicy {
	void reject(Runnable task,MyThreadPoolExecutor myThreadPoolExecutor);
}
