package br.com.rcrios.smartportfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
  List<Portfolio> findByMutualFundId(Long mfId);

  Optional<Portfolio> findFirstByNameIgnoreCase(String name);

  @Query("SELECT p FROM Portfolio p WHERE p.master = null")
  Optional<Portfolio> getRootPortfolio();
}
