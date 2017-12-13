/*
 * This file is part of JuniperBotJ.
 *
 * JuniperBotJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBotJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBotJ. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.caramel.juniperbot.web.ranking.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.caramel.juniperbot.model.exception.NotFoundException;
import ru.caramel.juniperbot.ranking.model.RankingInfo;
import ru.caramel.juniperbot.ranking.service.RankingService;
import ru.caramel.juniperbot.security.utils.SecurityUtils;
import ru.caramel.juniperbot.web.common.AbstractController;
import ru.caramel.juniperbot.web.common.navigation.Navigation;
import ru.caramel.juniperbot.web.common.navigation.PageElement;

import java.util.Collections;
import java.util.List;

@Controller
public class RankingController extends AbstractController {

    @Autowired
    private RankingService rankingService;

    @RequestMapping("/ranking/{serverId}")
    @Navigation(PageElement.RANKING)
    public ModelAndView view(@PathVariable long serverId) {
        ModelAndView mv;
        List<RankingInfo> members = rankingService.getRankingInfos(serverId);
        if (SecurityUtils.isAuthenticated() && isGuildAuthorized(serverId)) {
            mv = createModel("ranking.admin", serverId);
        } else {
            mv = createModel("ranking.user", serverId, false);
            if (CollectionUtils.isEmpty(members)) {
                throw new NotFoundException();
            }
        }
        return mv.addObject("members", members);
    }
}