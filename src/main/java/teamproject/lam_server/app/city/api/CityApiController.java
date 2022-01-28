package teamproject.lam_server.app.city.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.lam_server.app.city.dto.view.CityFoodAndViewResponse;
import teamproject.lam_server.app.city.dto.view.CityGridDataResponse;
import teamproject.lam_server.app.city.dto.view.CitySlideResponse;
import teamproject.lam_server.app.city.dto.view.TotalCityInfoResponse;
import teamproject.lam_server.app.city.service.query.CityQueryService;
import teamproject.lam_server.constants.CategoryConstants.CityName;
import teamproject.lam_server.global.dto.MenuResponse;
import teamproject.lam_server.global.dto.Result;
import teamproject.lam_server.global.service.MenuService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/city")
public class CityApiController {
    private final CityQueryService cityQueryService;
    private final MenuService menuService;

    /**
     * dependence presentation layer::home(header)
     * -> menu bar
     */
    @GetMapping("/names")
    public ResponseEntity<Result> getCityNames() {
        MenuResponse response = menuService.getCityMenus();
        return ResponseEntity.ok().body(new Result(response));
    }

    /**
     * dependence presentation layer::home(body)
     * -> slide image(top)
     */
    @GetMapping("/slide-images")
    public ResponseEntity<Result> cityMainSlideImages() {
        List<CitySlideResponse> citySlideResponse = cityQueryService.searchCityImage();
        return ResponseEntity.ok().body(new Result(citySlideResponse));
    }

    /**
     * dependence presentation layer::home(body)
     * -> grid info(bottom)
     */
    @GetMapping("/grid-infos")
    public ResponseEntity<Result> searchCityGridInfos() {
        List<CityGridDataResponse> cityGridResponse = cityQueryService.searchCurrentCityInfo();
        return ResponseEntity.ok().body(new Result(cityGridResponse));
    }

    /**
     * dependence presentation layer::city(body)
     * -> total city info tab pane(top)
     */
    @GetMapping("{cityName}/total-infos")
    public ResponseEntity<Result> getTotalCityInfo(@PathVariable("cityName") CityName cityName) {
        TotalCityInfoResponse totalCityInfoResponse = cityQueryService.searchTotalCityInfo(cityName);
        return ResponseEntity.ok().body(new Result(totalCityInfoResponse));
    }

    /**
     * dependence presentation layer::city(body)
     * -> food & view image slide(bottom)
     */
    @GetMapping("{cityName}/foods-and-view")
    public ResponseEntity<Result> getCityFoodAndViewInfo(@PathVariable("cityName") CityName cityName) {
        CityFoodAndViewResponse cityFoodAndViewResponses = cityQueryService.searchCityFoodAndView(cityName);
        return ResponseEntity.ok().body(new Result(cityFoodAndViewResponses));
    }


}
