package br.com.company.logistics.project.configuration;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.company.logistics.project.configuration.RoutingDataSource;

public class ReadTransactionTemplate extends TransactionTemplate {

    ReadTransactionTemplate(final PlatformTransactionManager transactionManager) {
        super(transactionManager);
        setReadOnly(true);
    }

    @Override
    public <T> T execute(final TransactionCallback<T> action) {
        RoutingDataSource.setReadRoute();
        try {
            return super.execute(action);
        } finally {
            RoutingDataSource.setWriteRoute();
        }
    }
}
