package mcnc.survwey.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    void globalSetUp() throws Exception {
        Resource resource = new ClassPathResource("schema.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);

        Resource dataResource = new ClassPathResource("data.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), dataResource);
    }
}
