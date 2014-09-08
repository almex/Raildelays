package be.raildelays.domain.railtime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class Station implements Serializable {

    private static final long serialVersionUID = -187269999487626223L;

    @NotNull
    @Size(min = 1, max = 256)
    private String name;

    public Station(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("station=[");

        builder.append(name);

        builder.append("]");
        return builder.toString();
    }

}
