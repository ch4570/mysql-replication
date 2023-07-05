package replication.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import replication.test.config.properties.ReplicaSourceNames;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private ReplicaSourceNames replicaSourceNames;

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        List<String> replicationSources = targetDataSources.keySet().stream()
                .map(Object::toString)
                .filter(string -> string.contains("slave"))
                .collect(Collectors.toList());

        this.replicaSourceNames = new ReplicaSourceNames(replicationSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly) {
            String sourceName = replicaSourceNames.getNext();

            log.info("SourceName = {}", sourceName);
            return sourceName;
        }

        return "master";
    }
}
