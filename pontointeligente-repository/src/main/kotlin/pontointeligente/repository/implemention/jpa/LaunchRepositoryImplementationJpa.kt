package pontointeligente.repository.implemention.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import pontointeligente.repository.contract.LaunchRepository
import java.time.LocalDate

@Repository("launchRepositoryJpa")
interface LaunchRepositoryImplementationJpa : JpaRepository<Launch, String>, LaunchRepository {

    @Query(
        "select l" +
                "  from Launch  l" +
                " where l.employeeCpf = :employeeCpf"
    )
    override fun findLaunchByEmployee(employeeCpf: String): List<Launch>

    @Query(
        "select l" +
                "  from Launch  l" +
                " where l.employeeCpf = :employeeCpf" +
                "   and day(l.dateOfLaunch) = day(:dateLaunch)" +
                "   and month(l.dateOfLaunch) = month(:dateLaunch)" +
                "   and year(l.dateOfLaunch) = year(:dateLaunch)" +
                "   and l.type = :type"
    )
    override fun findLaunchByEmployeeDateAndType(employeeCpf: String, dateLaunch: LocalDate, type: TypeEnum): Launch?
}