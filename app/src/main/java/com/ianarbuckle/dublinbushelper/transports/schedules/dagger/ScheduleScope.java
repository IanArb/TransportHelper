package com.ianarbuckle.dublinbushelper.transports.schedules.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Ian Arbuckle on 12/08/2017.
 *
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@interface ScheduleScope {
}
