/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

/**
 * @brief Base class for any kind of producer of streams.
 *
 * This can be an LLM that is taking model's output and pushing it into a stream.
 * It exposes a function which will be called to push data to streams.
 */
class StreamProducer {
 public:
  /**
   * @brief Push data to the associated streams.
   *
   * This function should be implemented by derived classes to handle the logic
   * for pushing data into one or more streams.
   */
  virtual void push_data_to_streams() = 0;

  /**
   * @brief Virtual destructor.
   */
  virtual ~StreamProducer() = default;
};