package com.zx.sms.common;

import io.netty.util.concurrent.Future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.LockSupport;

import org.junit.Test;

import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;

public class TestCirculeFutureTask {
	int cnt = 0;
	@Test
	public void test() {
		final Thread th = Thread.currentThread();
		EventLoopGroupFactory.INS.submitUnlimitCircleTask( new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				cnt++;
				System.out.println(cnt);
				return cnt;
			}
		}, new ExitUnlimitCirclePolicy() {

			@Override
			public boolean notOver(Future future) {
				try {
					boolean ret =  (Integer)future.get() < 100;
					if(!ret){
						LockSupport.unpark(th);
					}
					return ret;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LockSupport.unpark(th);
				return false;
			}
		},1);
		System.out.println("==========");
		LockSupport.park();

	}

}
