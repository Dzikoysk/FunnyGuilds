package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
final class ManageValidator implements Validator<CanManage, User, ValidationException> {

    private final MemberValidator memberValidator = new MemberValidator();

    @Override
    public boolean validate(Context context, CanManage annotation, Property property, User user) throws ValidationException {
        this.memberValidator.isMember(user);

        if (!user.canManage()) {
            throw new InternalValidationException(config -> config.commands.validation.notOwner);
        }

        return true;
    }

    @Override
    public Class<CanManage> getAnnotation() {
        return CanManage.class;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

}
