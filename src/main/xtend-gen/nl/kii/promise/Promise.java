package nl.kii.promise;

import com.google.common.base.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import nl.kii.async.annotation.Atomic;
import nl.kii.promise.IPromise;
import nl.kii.promise.PromiseException;
import nl.kii.stream.Entry;
import nl.kii.stream.Value;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * A Promise is a publisher of a value. The value may arrive later.
 */
@SuppressWarnings("all")
public class Promise<T extends Object> implements IPromise<T> {
  /**
   * Property to see if the promise is fulfulled
   */
  @Atomic
  private final AtomicBoolean _fulfilled = new AtomicBoolean(false);
  
  /**
   * The result of the promise, if any, otherwise null
   */
  @Atomic
  private final AtomicReference<Entry<T>> _entry = new AtomicReference<Entry<T>>();
  
  /**
   * Lets others listen for the arrival of a value
   */
  @Atomic
  private final AtomicReference<Procedure1<T>> _valueFn = new AtomicReference<Procedure1<T>>();
  
  /**
   * Always called, both when there is a value and when there is an error
   */
  @Atomic
  private final AtomicReference<Procedure1<Entry<T>>> _resultFn = new AtomicReference<Procedure1<Entry<T>>>();
  
  /**
   * Lets others listen for errors occurring in the onValue listener
   */
  @Atomic
  private final AtomicReference<Procedure1<Throwable>> _errorFn = new AtomicReference<Procedure1<Throwable>>();
  
  /**
   * Create a new unfulfilled promise
   */
  public Promise() {
  }
  
  /**
   * Create a fulfilled promise
   */
  public Promise(final T value) {
    this.set(value);
  }
  
  /**
   * Constructor for easily creating a child promise
   */
  public Promise(final Promise<?> parentPromise) {
    final Procedure1<Throwable> _function = new Procedure1<Throwable>() {
      public void apply(final Throwable it) {
        Promise.this.error(it);
      }
    };
    parentPromise.onError(_function);
  }
  
  /**
   * only has a value when finished, otherwise null
   */
  public Entry<T> get() {
    return this.getEntry();
  }
  
  /**
   * set the promised value
   */
  public Promise<T> set(final T value) {
    Promise<T> _xblockexpression = null;
    {
      boolean _equals = Objects.equal(value, null);
      if (_equals) {
        throw new NullPointerException("cannot promise a null value");
      }
      Value<T> _value = new Value<T>(value);
      this.apply(_value);
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  /**
   * report an error to the listener of the promise.
   */
  public Promise<T> error(final Throwable t) {
    nl.kii.stream.Error<T> _error = new nl.kii.stream.Error<T>(t);
    this.apply(_error);
    return this;
  }
  
  public void apply(final Entry<T> it) {
    try {
      boolean _equals = Objects.equal(it, null);
      if (_equals) {
        throw new NullPointerException("cannot promise a null entry");
      }
      Boolean _fulfilled = this.getFulfilled();
      if ((_fulfilled).booleanValue()) {
        throw new PromiseException(("cannot apply an entry to a completed promise. entry was: " + it));
      }
      this.setFulfilled(Boolean.valueOf(true));
      Procedure1<T> _valueFn = this.getValueFn();
      boolean _notEquals = (!Objects.equal(_valueFn, null));
      if (_notEquals) {
        this.publish(it);
      } else {
        this.setEntry(it);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * If the promise recieved or recieves an error, onError is called with the throwable
   */
  public Promise<T> onError(final Procedure1<Throwable> errorFn) {
    Promise<T> _xblockexpression = null;
    {
      this.setErrorFn(errorFn);
      _xblockexpression = this;
    }
    return _xblockexpression;
  }
  
  /**
   * Always call onResult, whether the promise has been either fulfilled or had an error.
   */
  public Promise<T> always(final Procedure1<Entry<T>> resultFn) {
    try {
      Promise<T> _xblockexpression = null;
      {
        Procedure1<Entry<T>> _resultFn = this.getResultFn();
        boolean _notEquals = (!Objects.equal(_resultFn, null));
        if (_notEquals) {
          throw new PromiseException("cannot listen to promise.always more than once");
        }
        this.setResultFn(resultFn);
        _xblockexpression = this;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Call the passed onValue procedure when the promise has been fulfilled with value. This also starts the onError and always listening.
   */
  public void then(final Procedure1<T> valueFn) {
    try {
      Procedure1<T> _valueFn = this.getValueFn();
      boolean _notEquals = (!Objects.equal(_valueFn, null));
      if (_notEquals) {
        throw new PromiseException("cannot listen to promise.then more than once");
      }
      this.setValueFn(valueFn);
      Boolean _fulfilled = this.getFulfilled();
      if ((_fulfilled).booleanValue()) {
        Entry<T> _entry = this.getEntry();
        this.publish(_entry);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected Entry<T> buffer(final Entry<T> value) {
    Entry<T> _xifexpression = null;
    Entry<T> _entry = this.getEntry();
    boolean _equals = Objects.equal(_entry, null);
    if (_equals) {
      _xifexpression = this.setEntry(value);
    }
    return _xifexpression;
  }
  
  /**
   * Send an entry directly (no queue) to the listeners
   * (onValue, onError, onFinish). If a value was processed,
   * ready is set to false again, since the value was published.
   */
  protected void publish(final Entry<T> it) {
    boolean _matched = false;
    if (!_matched) {
      if (it instanceof Value) {
        _matched=true;
        Procedure1<Throwable> _errorFn = this.getErrorFn();
        boolean _notEquals = (!Objects.equal(_errorFn, null));
        if (_notEquals) {
          try {
            Procedure1<T> _valueFn = this.getValueFn();
            _valueFn.apply(((Value<T>)it).value);
          } catch (final Throwable _t) {
            if (_t instanceof Throwable) {
              final Throwable t = (Throwable)_t;
              Procedure1<Throwable> _errorFn_1 = this.getErrorFn();
              _errorFn_1.apply(t);
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        } else {
          Procedure1<T> _valueFn_1 = this.getValueFn();
          _valueFn_1.apply(((Value<T>)it).value);
        }
      }
    }
    if (!_matched) {
      if (it instanceof nl.kii.stream.Error) {
        _matched=true;
        Procedure1<Throwable> _errorFn = this.getErrorFn();
        boolean _notEquals = (!Objects.equal(_errorFn, null));
        if (_notEquals) {
          Procedure1<Throwable> _errorFn_1 = this.getErrorFn();
          _errorFn_1.apply(((nl.kii.stream.Error<T>)it).error);
        }
      }
    }
    Procedure1<Entry<T>> _resultFn = this.getResultFn();
    boolean _notEquals = (!Objects.equal(_resultFn, null));
    if (_notEquals) {
      try {
        Procedure1<Entry<T>> _resultFn_1 = this.getResultFn();
        _resultFn_1.apply(it);
      } catch (final Throwable _t) {
        if (_t instanceof Throwable) {
          final Throwable t = (Throwable)_t;
          Procedure1<Throwable> _errorFn = this.getErrorFn();
          _errorFn.apply(t);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
  }
  
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Promise { fulfilled: ");
    Boolean _fulfilled = this.getFulfilled();
    _builder.append(_fulfilled, "");
    _builder.append(", entry: ");
    Entry<T> _get = this.get();
    _builder.append(_get, "");
    _builder.append(" }");
    return _builder.toString();
  }
  
  public Boolean setFulfilled(final Boolean value) {
    return this._fulfilled.getAndSet(value);
  }
  
  public Boolean getFulfilled() {
    return this._fulfilled.get();
  }
  
  protected Entry<T> setEntry(final Entry<T> value) {
    return this._entry.getAndSet(value);
  }
  
  protected Entry<T> getEntry() {
    return this._entry.get();
  }
  
  protected Procedure1<T> setValueFn(final Procedure1<T> value) {
    return this._valueFn.getAndSet(value);
  }
  
  protected Procedure1<T> getValueFn() {
    return this._valueFn.get();
  }
  
  protected Procedure1<Entry<T>> setResultFn(final Procedure1<Entry<T>> value) {
    return this._resultFn.getAndSet(value);
  }
  
  protected Procedure1<Entry<T>> getResultFn() {
    return this._resultFn.get();
  }
  
  protected Procedure1<Throwable> setErrorFn(final Procedure1<Throwable> value) {
    return this._errorFn.getAndSet(value);
  }
  
  protected Procedure1<Throwable> getErrorFn() {
    return this._errorFn.get();
  }
}
