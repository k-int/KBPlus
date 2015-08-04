package com.k_int.kbplus

import java.util.concurrent.ConcurrentHashMap

class ExecutorWrapperService {

	def executorService
	ConcurrentHashMap<Object,java.util.concurrent.FutureTask> activeFuture = [:]

	def processClosure(clos,owner){
		owner = "${owner.class.name}:${owner.id}"
		def existingFuture = activeFuture.get(owner)
		if(!existingFuture){
		      def future = executorService.submit(clos as java.util.concurrent.Callable)
		      activeFuture.put(owner,future)
		}else{
			if(existingFuture.isDone()){
				activeFuture.remove(owner)
				processClosure(clos,owner)
			}
		}
	}

	def hasRunningProcess(owner){
		owner = "${owner.class.name}:${owner.id}"
		if(activeFuture.get(owner) == null){
			return false
		}else if(activeFuture.get(owner).isDone()){
			activeFuture.remove(owner)
			return false
		}else if(activeFuture.get(owner).isDone() == false){
			return true
		}
	}
}

