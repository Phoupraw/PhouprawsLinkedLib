package phoupraw.mcmod.linked.api.yacl

import dev.isxander.yacl3.api.OptionFlag

/**
 * 被标记的配置项会加注[OptionFlag.GAME_RESTART]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class GameRestart
