package com.k_int.kbplus


class ExecutorWrapperService {

	def executorService
	HashMap<Object,java.util.concurrent.FutureTask> activeFuture = [:]

	def processClosure(clos,owner){
		owner = owner.toString()
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
		owner = owner.toString()
		def result = activeFuture.get(owner) !=null ? !activeFuture.get(owner).isDone() : false

		return result

	}
}

