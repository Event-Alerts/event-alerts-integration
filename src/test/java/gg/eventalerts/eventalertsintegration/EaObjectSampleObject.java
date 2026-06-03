package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

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
        public final Date date;
        @SuppressWarnings("unused")
        public final Double boxedDouble;
        @SuppressWarnings("unused")
        public final double primitiveDouble;
        @SuppressWarnings("unused")
        public final Integer boxedInteger;
        @SuppressWarnings("unused")
        public final int primitiveInteger;
        @SuppressWarnings("unused")
        public final Long boxedLong;
        @SuppressWarnings("unused")
        public final long primitiveLong;
        @SuppressWarnings("unused")
        public final ObjectId objectId;
        @SuppressWarnings("unused")
        public final UUID uuid;
        @SuppressWarnings("unused")
        public final CrossBan.Status status;
        @SuppressWarnings("unused")
        public final Set<String> stringSet;
        @SuppressWarnings("unused")
        public final Set<CrossBan.Status> statusSet;
        @SuppressWarnings("unused")
        public final Leaf leaf;

        Nested(
                String label,
                Date date,
                Double boxedDouble,
                double primitiveDouble,
                Integer boxedInteger,
                int primitiveInteger,
                Long boxedLong,
                long primitiveLong,
                ObjectId objectId,
                UUID uuid,
                CrossBan.Status status,
                Set<String> stringSet,
                Set<CrossBan.Status> statusSet,
                Leaf leaf) {
            this.label = label;
            this.date = date;
            this.boxedDouble = boxedDouble;
            this.primitiveDouble = primitiveDouble;
            this.boxedInteger = boxedInteger;
            this.primitiveInteger = primitiveInteger;
            this.boxedLong = boxedLong;
            this.primitiveLong = primitiveLong;
            this.objectId = objectId;
            this.uuid = uuid;
            this.status = status;
            this.stringSet = stringSet;
            this.statusSet = statusSet;
            this.leaf = leaf;
        }
    }

    static final class Leaf extends EAObject {
        @SuppressWarnings("unused")
        public final String name;
        @SuppressWarnings("unused")
        public final String optional;

        Leaf(String name, String optional) {
            this.name = name;
            this.optional = optional;
        }
    }
}
