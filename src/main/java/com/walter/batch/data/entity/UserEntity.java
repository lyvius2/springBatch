package com.walter.batch.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;

import java.io.Serializable;

@Getter
@SequenceGenerator(name = "userSeqGenerator", sequenceName = "userSeq", initialValue = 1, allocationSize = 1)
@Table(name = "user")
@Entity
public class UserEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeqGenerator")
	private long id;

	@Column(name = "name")
	private String name;
}
