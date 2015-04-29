package com.crazychen.candroid.cand.httputil.base;

/**
 * ���󻺴�ӿ�
 * 
 * @param <K> key������
 * @param <V> value����
 */
public interface Cache<K, V> {

    public V get(K key);

    public void put(K key, V value);

    public void remove(K key);

}

