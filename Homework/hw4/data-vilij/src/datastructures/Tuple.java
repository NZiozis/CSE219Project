package datastructures;


/**
 * This class is meant to deal with error handling and will determine from one of two errors, Duplicate and Invalid
 * errors.
 *
 * @param <T>  This will typically be an Integer that represents the line the error is on
 * @param <T1> This will typically be a String that holds the line with the error
 */
public class Tuple<T, T1>{
    private boolean _isDuplicate;
    private T       _key;
    private T1      _value;

    public Tuple(T key, T1 value){
        _key = key;
        _value = value;
    }

    public T get_key()                               { return _key; }

    public void set_key(T _key)                      { this._key = _key; }

    public boolean get_isDuplicate()                 { return _isDuplicate; }

    public void set_isDuplicate(boolean _isDuplicate){ this._isDuplicate = _isDuplicate; }

    public T1 get_value()                            { return _value; }

    public void set_value(T1 _value)                 { this._value = _value; }

}

