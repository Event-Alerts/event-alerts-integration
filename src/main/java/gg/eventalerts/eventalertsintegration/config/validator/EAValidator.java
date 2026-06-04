package gg.eventalerts.eventalertsintegration.config.validator;

import eu.okaeri.validator.OkaeriValidator;
import eu.okaeri.validator.policy.NullPolicy;
import gg.eventalerts.eventalertsintegration.config.validator.provider.DurationRangeProvider;
import gg.eventalerts.eventalertsintegration.config.validator.provider.PatternCollectionProvider;
import org.jetbrains.annotations.NotNull;


public class EAValidator extends OkaeriValidator {
    protected EAValidator(NullPolicy nullPolicy) {
        super(nullPolicy);
        register(new DurationRangeProvider());
        register(new PatternCollectionProvider());
    }

    @NotNull
    public static EAValidator of() {
        return of(NullPolicy.NULLABLE);
    }

    @NotNull
    public static EAValidator of(NullPolicy nullPolicy) {
        return new EAValidator(nullPolicy);
    }
}
