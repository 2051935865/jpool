package com.wtu.pool;

/**
 * ������ǰ����
 * DiscardRejectPolicy.java
 * Author: �ŷ�����
 * Date: 2019��11��5��
 * Description: TODO
 *
 */
public class DiscardRejectPolicy implements RejectPolicy{

	@Override
	public void reject(Runnable task, MyThreadPoolExecutor myThreadPoolExecutor) {
		System.out.println("discard one task");
	}

}
