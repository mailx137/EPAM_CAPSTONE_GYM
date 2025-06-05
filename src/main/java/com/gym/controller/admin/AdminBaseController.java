package com.gym.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public abstract class AdminBaseController {
}
