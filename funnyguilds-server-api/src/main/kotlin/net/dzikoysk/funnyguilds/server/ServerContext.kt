package net.dzikoysk.funnyguilds.server

import net.dzikoysk.funnyguilds.server.command.FunnyCommand
import net.dzikoysk.funnyguilds.server.command.FunnyCommandFactory
import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import net.dzikoysk.funnyguilds.server.event.FunnyEvent
import net.dzikoysk.funnyguilds.server.event.FunnyEventPriority
import kotlin.reflect.KClass

interface ServerContext {

    fun getOnlinePlayers(): Collection<FunnyPlayer>

    fun <EVENT : FunnyEvent> registerListener(eventType: KClass<EVENT>, priority: FunnyEventPriority, listener: (EVENT) -> Unit)

    fun <EVENT : FunnyEvent> callEvent(event: EVENT)

    fun registerCommand(pattern: String, permission: String, command: FunnyCommand)

    fun registerCommand(commandFactory: FunnyCommandFactory) =
        with (commandFactory.create()) {
            registerCommand(pattern, permission, command)
        }

}

inline fun <reified EVENT : FunnyEvent> ServerContext.registerListener(priority: FunnyEventPriority, noinline listener: (EVENT) -> Unit) =
    registerListener(EVENT::class, priority, listener)