package com.wtu.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * MyThreadPoolExecutor.java
 * Author: �ŷ�����
 * Date: 2019��11��4��
 * Description: TODO
 *
 */
public class MyThreadPoolExecutor implements Executor {


	/**
	 * �̳߳�����
	 */
	private String name;
	
	/**
	 * �߳����к�
	 */
	private AtomicInteger sequence = new AtomicInteger(0);
	
	/**
	 * �����߳���
	 */
	private int coreSize;
	
	/**
	 * ����߳���
	 */
	private int maxSize;
	
	/**
	 * �������
	 */
	private BlockingQueue<Runnable> taskQueue;
	
	/**
	 * �ܾ�����
	 */
	private RejectPolicy rejectPolicy;
	
	/**
	   * ��ǰ�������е��߳���
	   * ��Ҫ�޸�ʱ�̼߳�������֪������ʹ��AtomicInteger
	   * ����Ҳ����ʹ��volatile�����Unsafe��CAS���� 
	 */
	private AtomicInteger runningCount= new AtomicInteger(0);
	
	public MyThreadPoolExecutor(String name,int coreSize,int maxSize,BlockingQueue<Runnable>taskQueue,RejectPolicy rejectPolicy) {
		this.name = name;
		this.coreSize = coreSize;
		this.maxSize = maxSize;
		this.taskQueue = taskQueue;
		this.rejectPolicy = rejectPolicy;
	}
	

	@Override
	public void execute(Runnable task) {
		//�������е��߳���
		int count = runningCount.get();
		//����������е��߳���С�ں����߳�������ôֱ�Ӽ�һ
		if(count<coreSize) {
			//ע�⣬���ﲻһ����ӳɹ���addWorker()�������滹Ҫ�ж�һ���ǲ���С
			if(addWorker(task,true)) {
				return ;
			}
		}
		//����ﵽ�˺����߳������ȳ������������
		//����֮����ʹ��offer()������Ϊ����������ˣ�offer()����������false
		if(taskQueue.offer(task)) {            
			//do nothing Ϊ���߼�����������������
		}else {
			//������ʧ�ܣ�˵���������ˣ��Ǿ����һ���Ǻ����߳�
			if(!addWorker(task,false)) {
				//����Ǻ����߳����ʧ�ܣ���ִ�оܾ�����
				rejectPolicy.reject(task,this);
			}
		}
	}
	
	private boolean addWorker(Runnable newTask,boolean core) {
		
		//��ѭ���ж��ǲ�����Ŀ��Դ���һ���߳�
		for(;;) {
			//�������е��߳���
			int count = runningCount.get();
			//�����̻߳��ǷǺ����߳�
			int max = core?coreSize:maxSize;
			//�����㴴���̵߳�������ֱ�ӷ���false
			if(count>=max) {
				return false;
			}
			//�޸�runningCount �ɹ������Դ����߳�
			if(runningCount.compareAndSet(count, count+1)) {
				//�̵߳�����
				String threadName=(core ? "core_":"")+" "+core+" "+name+sequence.incrementAndGet();
				//�����̲߳�����
				new Thread(() ->{
					System.out.println("thread name:"+Thread.currentThread().getName());
					//���е�����
					Runnable task = newTask;
					//���ϴ����������ȡ����ִ�У����ȡ��������Ϊnull,������ѭ�����߳�Ҳ�ͽ�����
					while(task!=null||(task=getTask())!=null) {
						try {
							//ִ������
							task.run();
						}finally {
							task =null;
						}
					}
				},threadName).start();
				break;
			}
		}
		return true;
	}
	
	/**
	 * �������
	 * @return
	 */
	private Runnable getTask() {
		try {
			//take()������һֱ������֪��ȡ������Ϊֹ
			return taskQueue.take();
		} catch (InterruptedException e) {
			//�߳��ж��ˣ�����null���Խ�����ǰ�߳�
			//��ǰ�̶߳�Ҫ�����ˣ���ӦҪ��runningCount��������1
			runningCount.decrementAndGet();
			return null;
		}
	}
}
