package com.oracle.tomcat.example.visitorsweb;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Visitor.class)
public class Visitor_
{
    public static volatile SingularAttribute<Visitor, Long> id;
    public static volatile SingularAttribute<Visitor, String> name;
}