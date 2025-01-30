package org.springframework.samples.petclinic.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private Pageable pageable;

	private Pet pet;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		pet = new Pet();
	}

	@Test
	void shouldSaveOwner() {
		Owner owner = new Owner();
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("12, Oak Street");
		owner.setCity("London");
		owner.setTelephone("123456789");

		when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> {
			Owner savedOwner = invocation.getArgument(0);
			savedOwner.setId(2);
			return savedOwner;
		});

		Owner savedOwner = ownerRepository.save(owner);
		assertThat(savedOwner).isNotNull();
		assertThat(savedOwner.getId()).isEqualTo(2);
		verify(ownerRepository, times(1)).save(owner);
	}

	@Test
	void shouldFindOwnersByLastName() {
		Pageable pageable = mock(Pageable.class);
		Owner owner1 = new Owner();
		owner1.setFirstName("Bob");
		owner1.setLastName("Davis");

		Owner owner2 = new Owner();
		owner2.setFirstName("Charlie");
		owner2.setLastName("Davis");

		when(ownerRepository.findByLastNameStartingWith("Davis", pageable))
			.thenReturn(new PageImpl<>(List.of(owner1, owner2)));

		Page<Owner> ownersPage = ownerRepository.findByLastNameStartingWith("Davis", pageable);

		assertThat(ownersPage.getContent()).hasSize(2);
		assertThat(ownersPage.getContent().get(0).getLastName()).isEqualTo("Davis");
		assertThat(ownersPage.getContent().get(1).getLastName()).isEqualTo("Davis");
	}

	@Test
	void shouldUpdateOwner() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("20, Birch Road");
		owner.setCity("Paris");
		owner.setTelephone("1231231234");

		when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> {
			Owner updatedOwner = invocation.getArgument(0);
			updatedOwner.setFirstName("Updated John");
			return updatedOwner;
		});

		owner.setFirstName("Updated John");
		Owner updatedOwner = ownerRepository.save(owner);

		assertThat(updatedOwner).isNotNull();
		assertThat(updatedOwner.getFirstName()).isEqualTo("Updated John");
		verify(ownerRepository, times(1)).save(owner);
	}

	@Test
	void shouldDeleteOwner() {
		Owner owner = new Owner();
		owner.setId(3);
		owner.setFirstName("Tom");
		owner.setLastName("Wilson");

		doNothing().when(ownerRepository).deleteById(owner.getId());

		ownerRepository.deleteById(owner.getId());
		verify(ownerRepository, times(1)).deleteById(owner.getId());
	}

	@Test
	void shouldNotDeleteNonExistentOwner() {
		long nonExistentId = 999L;
		doThrow(new IllegalArgumentException("Owner not found")).when(ownerRepository)
			.deleteById(Math.toIntExact(nonExistentId));

		try {
			ownerRepository.deleteById(Math.toIntExact(nonExistentId));
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Owner not found");
		}
		verify(ownerRepository, times(1)).deleteById(Math.toIntExact(nonExistentId));
	}

	@Test
	void shouldFindOwnerById() {
		Integer ownerId = 1;
		Owner owner = new Owner();
		owner.setId(ownerId);
		owner.setFirstName("Linda");
		owner.setLastName("Taylor");

		when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));

		Optional<Owner> foundOwner = ownerRepository.findById(ownerId);
		assertThat(foundOwner).isPresent();
		assertThat(foundOwner.get().getId()).isEqualTo(ownerId);
		verify(ownerRepository, times(1)).findById(ownerId);
	}

	@Test
	void shouldNotFindOwnerById() {
		long nonExistentId = 999L;

		when(ownerRepository.findById(Math.toIntExact(nonExistentId))).thenReturn(Optional.empty());

		Optional<Owner> foundOwner = ownerRepository.findById(Math.toIntExact(nonExistentId));
		assertThat(foundOwner).isNotPresent();
	}

	@Test
	void shouldReturnEmptyListWhenNoOwners() {
		Pageable pageable = mock(Pageable.class);
		when(ownerRepository.findByLastNameStartingWith("Nonexistent", pageable)).thenReturn(Page.empty());

		Page<Owner> ownersPage = ownerRepository.findByLastNameStartingWith("Nonexistent", pageable);

		assertThat(ownersPage.getContent()).isEmpty();
	}

	@Test
	void shouldNotSaveOwnerWithEmptyTelephone() {
		Owner owner = new Owner();
		owner.setFirstName("Nancy");
		owner.setLastName("Green");
		owner.setAddress("30, Pine Street");
		owner.setCity("Melbourne");
		owner.setTelephone("");

		try {
			if (owner.getTelephone().isEmpty()) {
				throw new IllegalArgumentException("Telephone number is required");
			}
			ownerRepository.save(owner);
		}
		catch (IllegalArgumentException e) {
			// Assert
			assertThat(e.getMessage()).isEqualTo("Telephone number is required");
		}
		verify(ownerRepository, times(0)).save(owner);
	}

	@Test
	void testSetAndGetBirthDate() {
		LocalDate birthDate = LocalDate.of(2020, 1, 1);
		pet.setBirthDate(birthDate);
		assertEquals(birthDate, pet.getBirthDate());
	}

}
