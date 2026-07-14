package gg.eventalerts.eventalertsintegration.messages;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Include;
import eu.okaeri.configs.annotation.IncludePosition;
import eu.okaeri.validator.annotation.NotNull;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import xyz.srnyx.annoyingapi.file.okaeri.SubConfig;
import xyz.srnyx.annoyingapi.libs.javautilities.MapGenerator;
import xyz.srnyx.annoyingapi.message.AnnoyingMessages;
import xyz.srnyx.annoyingapi.message.json.message.JsonChatMessage;


@Include(value = AnnoyingMessages.class, position = IncludePosition.BEFORE)
public class EAMessages extends AnnoyingMessages {
    public EAMessages(@org.jetbrains.annotations.NotNull EventAlertsIntegration plugin) {
        super(plugin);
    }

    @Comment
    @NotNull public Command command = new Command(this);

    public static class Command extends SubConfig<EAMessages, EAMessages> {
        public Command(@org.jetbrains.annotations.NotNull EAMessages root) {
            super(root);
        }

        @NotNull public JsonChatMessage reload = getRoot().defaultMessage("%prefix%Plugin reloaded successfully!@@%p%%command%@@%command%");

        @Comment
        @NotNull public Linking linking = new Linking(this);

        @Comment
        @NotNull public CrossBan crossban = new CrossBan(this);

        @Comment
        @NotNull public Transfer transfer = new Transfer(this);

        public static class Linking extends SubConfig<EAMessages, Command> {
            public Linking(@org.jetbrains.annotations.NotNull Command root) {
                super(root);
            }

            @NotNull public Check check = new Check(this);

            @Comment
            @NotNull public Discord discord = new Discord(this);

            @Comment
            @NotNull public Minecraft minecraft = new Minecraft(this);

            public static class Check extends SubConfig<EAMessages, Linking> {
                public Check(@org.jetbrains.annotations.NotNull Linking root) {
                    super(root);
                }

                @Comment("Placeholders: %unlinked%")
                @NotNull public JsonChatMessage result = getRoot().defaultMessage("%prefix%Successfully kicked %s%%unlinked%%p% unlinked players of the %s%%total%%p% online!@@%p%%command%@@%command%");
            }

            public static class Discord extends SubConfig<EAMessages, Linking> {
                public Discord(@org.jetbrains.annotations.NotNull Linking root) {
                    super(root);
                }

                @NotNull public JsonChatMessage not_linked = getRoot().defaultMessage("%prefix%%se%%input%%pe% is not linked to a Discord account!@@%pe%%command%@@%command%");

                @Comment
                @Comment("Placeholders: %username%, %id%")
                @NotNull public JsonChatMessage linked = getRoot().defaultMessage(MapGenerator.LINKED_HASH_MAP.mapOf(
                        "suggest_input", "%prefix%%s%%input%%p% is linked to @@%p%%command%@@%command%",
                        "copy_username", "%s%%username%@@%p%%id%@@%username% (%id%)"));
            }

            public static class Minecraft extends SubConfig<EAMessages, Linking> {
                public Minecraft(@org.jetbrains.annotations.NotNull Linking root) {
                    super(root);
                }

                @NotNull public JsonChatMessage not_linked = getRoot().defaultMessage("%prefix%%se%%input%%pe% is not linked to a Minecraft account!@@%pe%%command%@@%command%");

                @Comment
                @Comment("Placeholders: %username%, %uuid%")
                @NotNull public JsonChatMessage linked = getRoot().defaultMessage(MapGenerator.LINKED_HASH_MAP.mapOf(
                        "suggest_input", "%prefix%%s%%input%%p% is linked to @@%p%%command%@@%command%",
                        "copy_username", "%s%%username%@@%p%%uuid%@@%username% (%uuid%)"));
            }
        }

        public static class CrossBan extends SubConfig<EAMessages, Command> {
            public CrossBan(@org.jetbrains.annotations.NotNull Command root) {
                super(root);
            }

            @NotNull public Check check = new Check(this);

            public static class Check extends SubConfig<EAMessages, CrossBan> {
                public Check(@org.jetbrains.annotations.NotNull CrossBan root) {
                    super(root);
                }

                @Comment("Placeholders: %banned%")
                @NotNull public JsonChatMessage result = getRoot().defaultMessage("%prefix%Successfully kicked %s%%banned%%p% banned players!@@%p%%command%@@%command%");
            }
        }

        public static class Transfer extends SubConfig<EAMessages, Command> {
            public Transfer(@org.jetbrains.annotations.NotNull Command root) {
                super(root);
            }

            @Comment("Placeholders: %server%")
            @NotNull public JsonChatMessage transferring = getRoot().defaultMessage("%prefix%Transferring you to %s%%server%%p%...@@%p%%command%@@%command%");

            @Comment
            @NotNull public JsonChatMessage disabled = getRoot().defaultMessage("%prefix%%pe%Server transferring is only available on 1.20.5 or higher!@@%pe%%command%@@%command%");
        }
    }
}
