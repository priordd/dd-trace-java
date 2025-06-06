package datadog.trace.instrumentation.hibernate;

import datadog.trace.api.naming.SpanNaming;
import datadog.trace.bootstrap.instrumentation.api.InternalSpanTypes;
import datadog.trace.bootstrap.instrumentation.api.UTF8BytesString;
import datadog.trace.bootstrap.instrumentation.decorator.OrmClientDecorator;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HibernateDecorator extends OrmClientDecorator {
  public static final CharSequence HIBERNATE_SESSION = UTF8BytesString.create("hibernate.session");
  private static final String SERVICE_NAME =
      SpanNaming.instance().namingSchema().database().service("hibernate");
  public static final HibernateDecorator DECORATOR = new HibernateDecorator();

  @Override
  protected String service() {
    return SERVICE_NAME;
  }

  @Override
  protected String[] instrumentationNames() {
    return new String[] {"hibernate-core"};
  }

  @Override
  protected CharSequence spanType() {
    return InternalSpanTypes.HIBERNATE;
  }

  @Override
  protected CharSequence component() {
    return "java-hibernate";
  }

  @Override
  protected String dbType() {
    return null;
  }

  @Override
  protected String dbUser(final Object o) {
    return null;
  }

  @Override
  protected String dbInstance(final Object o) {
    return null;
  }

  @Override
  protected String dbHostname(Object o) {
    return null;
  }

  @Override
  public String entityName(final Object entity) {
    if (entity == null) {
      return null;
    }
    String name = null;
    final Set<String> annotations = new HashSet<>();
    for (final Annotation annotation : entity.getClass().getDeclaredAnnotations()) {
      annotations.add(annotation.annotationType().getName());
    }

    if (entity instanceof String) {
      // We were given an entity name, not the entity itself.
      name = (String) entity;
    } else if (annotations.contains("javax.persistence.Entity")) {
      // We were given an instance of an entity.
      name = entity.getClass().getName();
    } else if (entity instanceof List && !((List) entity).isEmpty()) {
      // We have a list of entities.
      name = entityName(((List) entity).get(0));
    }

    return name;
  }
}
