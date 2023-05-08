package io.github.betterclient.client.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static EventBus instance;

    private final List<Subscriber> eventSubscribers = new ArrayList<>();

    public EventBus(){
        instance = this;
    }

    public void subscribe(Object subscriber) {
        List<Method> methods = new ArrayList<>();

        for(Method md : subscriber.getClass().getMethods()){
            if(md.isAnnotationPresent(EventTarget.class) && md.getParameters().length == 1 && md.getParameters()[0].getType().getSuperclass() != null && md.getParameters()[0].getType().getSuperclass().getName().equalsIgnoreCase(Event.class.getName())) {
                methods.add(md);
            }
        }

        eventSubscribers.add(new Subscriber(subscriber, methods));
    }

    public void unSubscribe(Object unsubscriber) {
        eventSubscribers.removeIf(subscriber -> subscriber.instance == unsubscriber);
    }

    public void call(Event e) {
        eventSubscribers.forEach(subscriber ->
                subscriber.subscribedMethods.forEach(method -> {
                    if(method.getParameterTypes()[0].isAssignableFrom(e.getClass())){
                        try {
                            method.invoke(subscriber.instance, e);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }
                })
        );
    }

    static class Subscriber{
        private Object instance;
        private List<Method> subscribedMethods;

        public Subscriber(Object instance, List<Method> subscribedMethods) {
            this.instance = instance;
            this.subscribedMethods = subscribedMethods;
        }
    }
}
