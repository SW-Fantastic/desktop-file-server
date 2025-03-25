module swdc.rmdisk {

    requires java.desktop;
    requires swdc.application.dependency;
    requires swdc.application.configs;
    requires swdc.application.data;
    requires swdc.application.fx;

    requires org.hibernate.orm.core;
    requires swdc.commons;

    requires jakarta.annotation;
    requires jakarta.inject;
    requires java.persistence;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.media;
    requires org.controlsfx.controls;

    requires io.vertx.core;
    requires io.vertx.web;
    requires org.slf4j;
    requires java.sql;

    requires org.dom4j;
    requires password4j;
    requires jcifs;
    requires com.github.benmanes.caffeine;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jjwt.api;
    requires jjwt.impl;
    requires jjwt.jackson;
    requires org.apache.tika.core;

    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    opens org.swdc.rmdisk.client to
            com.fasterxml.jackson.databind,
            com.fasterxml.jackson.core;

    opens org.swdc.rmdisk.service.verticle.dav to
            io.vertx.core,
            io.vertx.web,
            swdc.application.dependency,
            swdc.application.data,
            swdc.application.fx;

    opens org.swdc.rmdisk.service.verticle to
            io.vertx.core,
            io.vertx.web,
            swdc.application.dependency,
            swdc.application.data,
            swdc.application.fx;

    opens org.swdc.rmdisk.views.modals to
            javafx.fxml,
            javafx.graphics,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk.service.verticle.http to
            io.vertx.core,
            io.vertx.web,
            swdc.commons,
            com.fasterxml.jackson.databind,
            com.fasterxml.jackson.core,
            swdc.application.dependency;

    opens org.swdc.rmdisk.core to
            org.controlsfx.controls,
            swdc.application.configs,
            swdc.application.dependency,
            swdc.application.fx,
            swdc.application.data;

    opens org.swdc.rmdisk.service.verticle.ftp to
            swdc.application.dependency;

    opens org.swdc.rmdisk.client.descriptor to
            swdc.application.dependency;

    opens org.swdc.rmdisk.core.repo.filters to
            swdc.application.data;

    opens org.swdc.rmdisk.core.dav to
            com.fasterxml.jackson.databind,
            com.fasterxml.jackson.core;

    opens org.swdc.rmdisk.views to
            javafx.graphics,
            javafx.fxml,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk.views.previews to
            javafx.graphics,
            javafx.fxml,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk.views.common to
            javafx.graphics,
            javafx.fxml,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk.views.cells to
            javafx.fxml,
            javafx.graphics,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk to
            javafx.graphics,
            javafx.fxml,
            swdc.application.configs,
            swdc.application.dependency,
            swdc.application.fx;

    opens org.swdc.rmdisk.core.log to
            ch.qos.logback.core,
            ch.qos.logback.classic;

    opens org.swdc.rmdisk.client.protocol to
            com.fasterxml.jackson.core,
            com.fasterxml.jackson.databind;

    opens org.swdc.rmdisk.core.xmlns to
            com.fasterxml.jackson.core,
            com.fasterxml.jackson.databind;

    opens org.swdc.rmdisk.core.dav.locks to
            com.fasterxml.jackson.core,
            com.fasterxml.jackson.databind;

    opens org.swdc.rmdisk.core.dav.multiple to
            com.fasterxml.jackson.core,
            com.fasterxml.jackson.databind;

    opens org.swdc.rmdisk.core.entity;
    opens org.swdc.rmdisk.service;

    opens database to swdc.application.data;

    opens icons;
    opens lang;
    opens views.main;
    opens views.modals;


}