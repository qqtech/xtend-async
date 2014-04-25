package nl.kii.util;

import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

public class SynchronizeExt {
	
	/**
	 * Apply Java synchronized block on an object and apply the procedure on it
	 */
	public static <T> void synchronize(T obj, Procedure0 proc) {
		synchronized (obj) {
			proc.apply();
		}
	}

	/**
	 * Apply Java synchronized block on an object and apply the procedure on it
	 */
	public static <T, R> R synchronize(T obj, Function0<R> proc) {
		synchronized (obj) {
			return proc.apply();
		}
	}	

	/**
	 * Apply Java synchronized block on an object and apply the procedure on it.
	 * Passes the object into the function.
	 */
	public static <T, R> R synchronize(T obj, Function1<T, R> proc) {
		synchronized (obj) {
			return proc.apply(obj);
		}
	}	
	
	
	/**
	 * Create a new synchronized function out of an existing one
	 */
	public static <T> Procedure1<T> synchronize(Procedure1<T> p) {
		final Procedure1<T> p2 = p;
		return new Procedure1<T>() {

			@Override
			public void apply(T o) {
				synchronized(o) {
					p2.apply(o);
				}
			}
			
		};
	}

	/**
	 * Create a new synchronized function out of an existing one
	 */
	public static <T, R> Function1<T, R> synchronize(Function1<T, R> fn) {
		final Function1<T, R> fn2 = fn;
		return new Function1<T, R>() {

			@Override
			public R apply(T p) {
				synchronized(p) {
					return fn2.apply(p);
				}
			}
			
		};
	}

	/**
	 * Create a new synchronized function out of an existing one
	 */
	public static <T1, T2, R> Function2<T1, T2, R> synchronize(Function2<T1, T2, R> fn) {
		final Function2<T1, T2, R> fn2 = fn;
		return new Function2<T1, T2, R>() {

			@Override
			public R apply(T1 p1, T2 p2) {
				synchronized(p1) {
					synchronized(p2) {
						return fn2.apply(p1, p2);
					}
				}
			}
			
		};
	}

}