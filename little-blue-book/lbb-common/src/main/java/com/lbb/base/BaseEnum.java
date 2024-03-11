package com.lbb.base;

public interface BaseEnum<E extends Enum<?>, T> {

	public T getCode();

	public String getMessage();

}
