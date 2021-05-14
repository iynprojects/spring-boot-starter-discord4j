/*
 * This file is part of spring-boot-starter-discord4j.
 *
 * spring-boot-starter-discord4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spring-boot-starter-discord4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with spring-boot-starter-discord4j.  If not, see <https://www.gnu.org/licenses/>.
 */
package discord4j.spring.event.context;

import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import reactor.util.annotation.Nullable;

final class EventScope implements Scope {

    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        final EventContext eventContext = EventContext.threadLocal();
        final Map<String, Object> objects = eventContext.getObjects();

        return objects.computeIfAbsent(name, ignored -> objectFactory.getObject());
    }

    @Override
    public Object remove(final String name) {
        final EventContext eventContext = EventContext.threadLocal();
        final Map<String, Runnable> destructionCallbacks = eventContext.getDestructionCallbacks();
        final Map<String, Object> objects = eventContext.getObjects();

        destructionCallbacks.remove(name);
        return objects.remove(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        final EventContext eventContext = EventContext.threadLocal();
        final Map<String, Runnable> destructionCallbacks = eventContext.getDestructionCallbacks();

        destructionCallbacks.put(name, callback);
    }

    @Nullable
    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    @Nullable
    @Override
    public String getConversationId() {
        return null;
    }
}
