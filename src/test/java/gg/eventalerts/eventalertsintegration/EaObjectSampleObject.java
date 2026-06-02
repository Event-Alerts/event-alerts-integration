package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.objects.EAObject;

final class EaObjectSampleObject extends EAObject {
    @SuppressWarnings("unused")
    public final String name;
    @SuppressWarnings("unused")
    public final String optional;
    @SuppressWarnings("unused")
    public final Nested nested;

    EaObjectSampleObject(String name, String optional, Nested nested) {
        this.name = name;
        this.optional = optional;
        this.nested = nested;
    }

    static final class Nested extends EAObject {
        @SuppressWarnings("unused")
        public final String label;
        @SuppressWarnings("unused")
        public final int count;

        Nested(String label, int count) {
            this.label = label;
            this.count = count;
        }
    }
}
