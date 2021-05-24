package web.model;

/**
 * Task parameters, front-end and back-end data transmission objects.
 *
 * @version 2.0
 * @date 2021/5/16 9:25
 */
public class SolverIdDTO {

    Long solverId;

    public SolverIdDTO() {

    }

    public SolverIdDTO(Long solverId) {
        this.solverId = solverId;
    }

    public Long getSolverId() {
        return solverId;
    }

    public void setSolverId(Long solverId) {
        this.solverId = solverId;
    }
}
