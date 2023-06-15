package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyModule
import net.dzikoysk.funnyguilds.InitContext
import net.dzikoysk.funnyguilds.server.ServerContext
import net.dzikoysk.funnyguilds.server.event.FunnyEventPriority.NORMAL
import net.dzikoysk.funnyguilds.server.event.FunnyJoinEvent
import net.dzikoysk.funnyguilds.server.registerListener
import net.dzikoysk.funnyguilds.user.model.SqlUserRepository
import net.dzikoysk.funnyguilds.user.model.UserDefinition
import net.dzikoysk.funnyguilds.user.model.UserId

class UserModule : FunnyModule {

    override fun onLoad(initContext: InitContext) {
        initContext.definitions.add(UserDefinition)
    }

    override fun onEnable(context: ServerContext, funnyGuilds: FunnyGuilds) {
        val userFacade = UserService(
            userRepository = SqlUserRepository(funnyGuilds.database)
        )

        funnyGuilds.registerComponent(userFacade)

        context.registerListener(NORMAL) { event: FunnyJoinEvent ->
            when (val user = userFacade.getUser(UserId(event.player.uniqueId))) {
                null -> {
                    val createdUser = userFacade.createUser(UserId(event.player.uniqueId), event.player.name)
                    event.player.sendMessage("Hello, ${createdUser.name}! Your UUID is ${createdUser.id}.")
                }
                else -> event.player.sendMessage("Hello again, ${user.name}! Your UUID is ${user.id}.")
            }
        }
    }

}