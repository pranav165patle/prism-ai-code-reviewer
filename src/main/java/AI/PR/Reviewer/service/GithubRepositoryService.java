package AI.PR.Reviewer.service;

import AI.PR.Reviewer.entity.GithubRepository;
import AI.PR.Reviewer.repository.GithubRepositoryRepository;
import org.springframework.stereotype.Service;
import AI.PR.Reviewer.dto.CreateRepositoryRequest;
import AI.PR.Reviewer.exception.RepositoryNotFoundException;
import AI.PR.Reviewer.exception.DuplicateRepositoryException;
import AI.PR.Reviewer.dto.UpdateRepositoryRequest;



@Service
public class GithubRepositoryService {

    private final GithubRepositoryRepository repository;

    public GithubRepositoryService(GithubRepositoryRepository repository) {
        this.repository = repository;
    }

    public GithubRepository saveRepository(CreateRepositoryRequest request) {

        if (repository.existsByFullName(request.getFullName())) {
            throw new DuplicateRepositoryException(
                    "Repository already exists: " + request.getFullName()
            );
        }

        GithubRepository githubRepository = new GithubRepository();

        githubRepository.setOwner(request.getOwner());
        githubRepository.setName(request.getName());
        githubRepository.setFullName(request.getFullName());
        githubRepository.setDefaultBranch(request.getDefaultBranch());

        return repository.save(githubRepository);
    }
    public java.util.List<GithubRepository> getAllRepositories() {
        return repository.findAll();
    }
    public GithubRepository getRepositoryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RepositoryNotFoundException(
                                "Repository not found with id: " + id
                        )
                );
    }
    public GithubRepository updateRepository(
            Long id,
            UpdateRepositoryRequest request) {

        GithubRepository githubRepository = repository.findById(id)
                .orElseThrow(() ->
                        new RepositoryNotFoundException(
                                "Repository not found with id: " + id
                        )
                );

        if (!githubRepository.getFullName().equals(request.getFullName())
                && repository.existsByFullName(request.getFullName())) {

            throw new DuplicateRepositoryException(
                    "Repository already exists: " + request.getFullName()
            );
        }

        githubRepository.setOwner(request.getOwner());
        githubRepository.setName(request.getName());
        githubRepository.setFullName(request.getFullName());
        githubRepository.setDefaultBranch(request.getDefaultBranch());

        return repository.save(githubRepository);
    }
    public void deleteRepository(Long id) {

        GithubRepository githubRepository = repository.findById(id)
                .orElseThrow(() ->
                        new RepositoryNotFoundException(
                                "Repository not found with id: " + id
                        )
                );

        repository.delete(githubRepository);
    }
}