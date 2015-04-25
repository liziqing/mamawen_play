package repo;

import models.InquiryReport;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ronald on 2015/3/21.
 */
public interface ReportRepository extends JpaRepository<InquiryReport, Long>{
}
