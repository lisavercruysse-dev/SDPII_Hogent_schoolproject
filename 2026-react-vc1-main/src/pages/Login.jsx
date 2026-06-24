import { useNavigate, useLocation } from "react-router";
import { useCallback } from "react";
import { FormProvider, useForm } from "react-hook-form";
import LabelInput from "../components/LabelInput";
import img from "../assets/delaware.png";
import { useAuth } from '../contexts/auth';
import Error from '../components/Error';

const validationRules = {
  email: {
    required: "Email is required",
  },
  password: {
    required: "Password is required",
  },
};

export default function Login() {
  const { error, loading, login } = useAuth();
  const navigate = useNavigate();
  const { search } = useLocation();

  const methods = useForm({
    defaultValues: {
      email: 'Jonas.VanAert@example.com',
      password: '12345678',
    },
  });

  const { handleSubmit, reset } = methods;

  const handleCancel = useCallback(() => {
    reset();
  }, [reset]);

  const handleLogin = useCallback(
    async ({email, password}) => {
      const loggedIn = await login(email, password);

      if (loggedIn) {
        const params = new URLSearchParams(search)
        navigate({
          pathname: params.get('redirect') || '/',
          replace: true,
        });
      }
    },
    [login, navigate, search],
  ); 

  return (
    <div className="relative h-screen bg-[#F9F9F9]">
      <div className="w-62.25 h-25.75 flex justify-center items-center">
        <img className="h-11.5 w-38.5" src={img} alt="delaware" />
      </div>
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
        <FormProvider {...methods}>
          <div
            className=" 
         mx-auto border border-[#E5E5E5] rounded-[10px] bg-white p-6.25 w-lg  "
          >
            <form
              className="flex flex-col gap-4 "
              onSubmit={handleSubmit(handleLogin)}
            >
              <div>
                <p className="card-title font-bold">Aanmelden</p>
                <Error error={error} />
                <p className="card-text">Vul uw email en wachtwoord in.</p>
              </div>
              <LabelInput
                label="Email"
                type="text"
                name="email"
                placeholder="your@email.com"
                validationRules={validationRules.email}
                data-cy="email_input"
              />
              <LabelInput
                label="Wachtwoord"
                type="password"
                placeholder="wachtwoord"
                name="password"
                validationRules={validationRules.password}
                data-cy="password_input"
              />
              {error && (
                <p className="text-[#FF4040] text-[14px]">
                  Ongeldig wachtwoord of email adres.
                </p>
              )}
              <div className="flex justify-end ">
                <button
                  type="button"
                  className="mr-2 secondary"
                  onClick={handleCancel}
                >
                  Annuleren
                </button>
                <button
                  type="submit"
                  className="primary"
                  disabled={loading}
                  data-cy="submit_btn"
                >
                  Log in
                </button>
              </div>
            </form>
          </div>
        </FormProvider>
      </div>
    </div>
  );
}
