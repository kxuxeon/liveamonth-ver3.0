import { defineStore } from "pinia";
import jwtDecode from "jwt-decode";
import AuthApiService from "@/services/AuthApiService";
import type { LoginType } from "@/modules/types/form/FormType";
import type { TokenType } from "@/modules/types/auth/AuthType";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token:
      ({} as TokenType) ||
      JSON.parse(
        localStorage.getItem("access-token") || String({} as TokenType)
      ),
  }),
  getters: {},
  actions: {
    async login(data: LoginType) {
      try {
        const response = await AuthApiService.login(data);
        const accessToken = jwtDecode(response.accessToken);
        console.log(accessToken);
      } catch (error) {
        console.log(error);
      }
    },
  },
});
