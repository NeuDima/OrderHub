package com.neupokoev.orderservice.mapper;

public interface Mapper<From, To> {

    To map(From from);
}
