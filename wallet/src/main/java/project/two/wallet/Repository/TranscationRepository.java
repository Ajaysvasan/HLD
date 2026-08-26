package project.two.wallet.Repository;

import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class TranscationRepository {
	private DataSource dataSource;

	public TranscationRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
