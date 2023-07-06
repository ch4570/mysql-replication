package replication.test.config.properties;

import lombok.Getter;

import java.util.List;

@Getter
public class ReplicaSourceNames {

    private final String[] value;
    private int count = 0;

    public ReplicaSourceNames(List<String> replicationSourceNames) {
        this(replicationSourceNames.toArray(String[]::new));
    }

    public ReplicaSourceNames(String[] value) {
        this.value = value;
    }

    public String getNext() {
        int index = count;
        count = (count + 1) % value.length;
        return value[index];
    }
}

