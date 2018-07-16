/*!
 * @copyright
 * Copyright (c) 2017-2018 Intel Corporation
 *
 * @copyright
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * @copyright
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * @copyright
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * */

#include "agent-framework/command/storage_commands.hpp"
#include "agent-framework/command/registry.hpp"


using namespace agent_framework::command;
using namespace agent_framework::module;

REGISTER_COMMAND(GetChassisInfo,
    [] (const GetChassisInfo::Request& request, GetChassisInfo::Response& response) {
        log_debug("storage-agent", "Getting chassis info.");
        response = get_manager<agent_framework::model::Chassis>().get_entry(request.get_uuid());
    }
);