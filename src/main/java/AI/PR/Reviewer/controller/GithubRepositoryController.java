package AI.PR.Reviewer.controller;

import AI.PR.Reviewer.entity.GithubRepository;
import AI.PR.Reviewer.service.GithubRepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import AI.PR.Reviewer.dto.CreateRepositoryRequest;
import jakarta.validation.Valid;
import AI.PR.Reviewer.dto.UpdateRepositoryRequest;

@RestController
@RequestMapping("/api/repositories")
public class GithubRepositoryController {

    private final GithubRepositoryService service;

    public GithubRepositoryController(GithubRepositoryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GithubRepository createRepository(
            @Valid @RequestBody CreateRepositoryRequest request) {

        return service.saveRepository(request);
    }
    @GetMapping
    public java.util.List<GithubRepository> getAllRepositories() {
        return service.getAllRepositories();
    }
    @GetMapping("/{id}")
    public GithubRepository getRepositoryById(@PathVariable Long id) {
        return service.getRepositoryById(id);
    }
    @PutMapping("/{id}")
    public GithubRepository updateRepository(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRepositoryRequest request) {

        return service.updateRepository(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRepository(@PathVariable Long id) {
        service.deleteRepository(id);
    }
}
