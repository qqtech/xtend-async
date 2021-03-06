package nl.kii.promise

import nl.kii.stream.message.Entry
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2
import nl.kii.observe.Observable
import nl.kii.util.Opt

interface IPromise<I, O> extends Procedure1<Entry<I, O>>, Observable<Entry<I, O>> {

	def Opt<Entry<I, O>> get()
	def boolean getFulfilled()

	def SubPromise<I, O> then(Procedure1<O> valueFn)
	def SubPromise<I, O> then(Procedure2<I, O> valueFn)

	def SubPromise<I, O> on(Class<? extends Throwable> exceptionType, boolean swallow, Procedure1<Throwable> errorFn)
	def SubPromise<I, O> on(Class<? extends Throwable> exceptionType, boolean swallow, Procedure2<I, Throwable> errorFn)

	def void setOperation(String operation)
	def String getOperation()
	
}
