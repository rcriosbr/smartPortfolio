package br.com.rcrios.smartportfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.LegalEntity;

@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, Long> {

  /**
   * Locates a LegalEntity by its national tax payer id (in Brazil, CNPJ).
   * 
   * @param nationalTaxPayerId
   *          Id that will be used to locate a LegalEntity.
   * 
   * @return A LegalEntity wrapped by an {@link Optional} container.
   */
  public Optional<LegalEntity> findByNationalTaxPayerId(String nationalTaxPayerId);
}
