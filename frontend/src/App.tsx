import {CssVarsProvider, extendTheme} from "@mui/joy/styles";
import CssBaseline from "@mui/joy/CssBaseline";
import {Navigate, Route, Routes} from "react-router";
import Sidebar from "./components/Navbar";
import Login from "./pages/Login";
import Register from "./pages/Register";
import {Toaster} from "react-hot-toast";
import TokenConfirmation from "./pages/TokenConfirmation";
import ResetPassword from "./pages/ResetPassword";
import usersApi from "./service/usersApi";
import Gallery from "./pages/Gallery.tsx";

const chakraTheme = extendTheme({
  colorSchemes: {
    light: {
      palette: {
        primary: {
          solidBg: "#319795",
          solidHoverBg: "#2C7A7B",
          solidActiveBg: "#285E61",
          outlinedColor: "#2C7A7B",
          outlinedBorder: "#2C7A7B",
          outlinedHoverBorder: undefined,
          outlinedHoverBg: "#E6FFFA",
          outlinedActiveBg: "#B2F5EA",
        },
        // neutral: {
        //   solidBg: "white",
        //   solidHoverBg: "#319795",
        //   solidActiveBg: "#2C7A7B",
        //   outlinedColor: "#2C7A7B",
        //   outlinedBorder: "#2C7A7B",
        //   outlinedHoverBorder: undefined,
        //   outlinedHoverBg: "#E6FFFA",
        //   outlinedActiveBg: "#B2F5EA",
        //   solidActiveColor: "white",
        //   solidHoverColor: "white",
        //   solidColor: "#319795",
        //   // set text color to red
        //   // textColor: "red",
        // },
        focusVisible: "rgba(66, 153, 225, 0.6)",
      },
    },
  },
  focus: {
    default: {
      outlineWidth: "3px",
    },
  },
  fontFamily: {
    body: "Inter, var(--chakra-fontFamily-fallback)",
  },
  components: {
    JoyButton: {
      styleOverrides: {
        root: ({ theme, ownerState }) => ({
          "&:focus": theme.focus.default,
          fontWeight: 600,
          ...(ownerState.size === "md" && {
            borderRadius: "0.375rem",
            paddingInline: "1rem",
          }),
        }),
      },
    },
  },
});

function App() {
  return (
    <CssVarsProvider disableTransitionOnChange theme={chakraTheme}>
      <div>
        <Toaster />
      </div>
      <CssBaseline />
      <div className="grid-container">
        <Sidebar />

        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/gallery" index element={<Gallery />} />
          <Route
            path="/registration-confirmation"
            element={<TokenConfirmation submitToken={usersApi.verifyUser} />}
          />
          <Route path="/forgot-password" element={<ResetPassword />} />
          <Route
            path="/password-reset"
            element={
              <TokenConfirmation submitToken={usersApi.verifyPasswordReset} />
            }
          />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </div>
    </CssVarsProvider>
  );
}

export default App;
