/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.kafka.streams;

import org.apache.kafka.streams.kstream.GlobalKTable;

import org.springframework.cloud.stream.binder.AbstractBinder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.binder.kafka.provisioning.KafkaTopicProvisioner;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsBinderConfigurationProperties;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsConsumerProperties;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsExtendedBindingProperties;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsProducerProperties;
import org.springframework.util.StringUtils;

/**
 * An {@link AbstractBinder} implementation for {@link GlobalKTable}.
 *
 * Provides only consumer binding for the bound {@link GlobalKTable}.
 * Output bindings are not allowed on this binder.
 *
 * @author Soby Chacko
 * @since 2.1.0
 */
public class GlobalKTableBinder extends
		AbstractBinder<GlobalKTable<Object, Object>, ExtendedConsumerProperties<KafkaStreamsConsumerProperties>, ExtendedProducerProperties<KafkaStreamsProducerProperties>>
		implements ExtendedPropertiesBinder<GlobalKTable<Object, Object>, KafkaStreamsConsumerProperties, KafkaStreamsProducerProperties> {

	private final KafkaStreamsBinderConfigurationProperties binderConfigurationProperties;

	private final KafkaTopicProvisioner kafkaTopicProvisioner;

	private final KafkaStreamsBindingInformationCatalogue kafkaStreamsBindingInformationCatalogue;

	private KafkaStreamsExtendedBindingProperties kafkaStreamsExtendedBindingProperties = new KafkaStreamsExtendedBindingProperties();

	public GlobalKTableBinder(KafkaStreamsBinderConfigurationProperties binderConfigurationProperties, KafkaTopicProvisioner kafkaTopicProvisioner,
				KafkaStreamsBindingInformationCatalogue kafkaStreamsBindingInformationCatalogue) {
		this.binderConfigurationProperties = binderConfigurationProperties;
		this.kafkaTopicProvisioner = kafkaTopicProvisioner;
		this.kafkaStreamsBindingInformationCatalogue = kafkaStreamsBindingInformationCatalogue;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Binding<GlobalKTable<Object, Object>> doBindConsumer(String name, String group, GlobalKTable<Object, Object> inputTarget,
															ExtendedConsumerProperties<KafkaStreamsConsumerProperties> properties) {
		if (!StringUtils.hasText(group)) {
			group = binderConfigurationProperties.getApplicationId();
		}
		KafkaStreamsConsumerBindingUtils.prepareConsumerBinding(name, group, inputTarget,
				getApplicationContext(),
				kafkaTopicProvisioner,
				kafkaStreamsBindingInformationCatalogue,
				binderConfigurationProperties, properties);
		return new DefaultBinding<>(name, group, inputTarget, null);
	}

	@Override
	protected Binding<GlobalKTable<Object, Object>> doBindProducer(String name, GlobalKTable<Object, Object> outboundBindTarget,
															ExtendedProducerProperties<KafkaStreamsProducerProperties> properties) {
		throw new UnsupportedOperationException("No producer level binding is allowed for GlobalKTable");
	}

	@Override
	public KafkaStreamsConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.kafkaStreamsExtendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public KafkaStreamsProducerProperties getExtendedProducerProperties(String channelName) {
		throw new UnsupportedOperationException("No producer binding is allowed and therefore no properties");
	}

}
