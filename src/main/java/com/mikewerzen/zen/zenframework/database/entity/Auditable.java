/*
 * Copyright 2019, Michael Werzen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Portions of this software are Copyright 2018, TessaTech LLC.
 *
 * Such portions are licensed under the MIT License (the "License"); you may not use this file
 *  except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/MIT
 */

package com.mikewerzen.zen.zenframework.database.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable
{
	@CreatedBy
	protected String createdBy;

	@CreatedDate
	protected Instant createdDate;

	@LastModifiedBy
	protected String lastModifiedBy;

	@LastModifiedDate
	@Version
	protected Instant lastModifiedDate;

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public Instant getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy()
	{
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy)
	{
		this.lastModifiedBy = lastModifiedBy;
	}

	public Instant getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}
}
