package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VarselKvitteringRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean existsById(UUID id) {
        return jdbcTemplate.queryForObject("select exists (select 1 from varsel_kvittering where id = ?)", Boolean.class, id);
    }

    public void insert(VarselKvittering varselKvittering) {
        jdbcTemplate.update("insert into varsel_kvittering (id, status) values (?, ?)", varselKvittering.getId(), varselKvittering.getStatus().name());
    }

    public Optional<VarselKvittering> findById(UUID id) {
        return Optional.ofNullable(
                DataAccessUtils.singleResult(
                        jdbcTemplate.query("select * from varsel_kvittering where id = ?", new VarselKvitteringRowMapper(), id)
                )
        );
    }

    private static class VarselKvitteringRowMapper implements RowMapper<VarselKvittering> {
        @Override
        public VarselKvittering mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new VarselKvittering(UUID.fromString(rs.getString("id")), VarselStatus.valueOf(rs.getString("status")));
        }
    }
}
