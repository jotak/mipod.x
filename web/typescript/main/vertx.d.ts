declare module vertx {
    interface MessageHandler<TMessage> {
        (message: TMessage, replier: MessageHandler<any>): void;
    }

    export class EventBus {
        constructor(address: string);
        publish(address: string, message: any): EventBus;
        send<T>(address: string, message: any, replyHandler?: MessageHandler<T>): EventBus;
        sendWithTimeout<T>(address: string, message: any, timeout: number, replyHandler?: MessageHandler<T>): EventBus;
        setDefaultReplyTimeout(millis: number): EventBus;
        registerHandler<T>(address: string, handler: MessageHandler<T>): EventBus;
        registerLocalHandler<T>(address: string, handler: MessageHandler<T>): EventBus;
        unregisterHandler<T>(address: string, handler: MessageHandler<T>): EventBus;
        onopen: any;
    }
}
